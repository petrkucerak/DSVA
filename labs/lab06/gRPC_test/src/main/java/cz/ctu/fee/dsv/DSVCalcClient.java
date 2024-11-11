package cz.ctu.fee.dsv;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSVCalcClient {
  private static final Logger logger = Logger.getLogger(DSVCalcClient.class.getName());

  private final ManagedChannel channel;
  private final CalculatorGrpc.CalculatorBlockingStub blockingStub;
  private final CalculatorGrpc.CalculatorStub asyncStub;

  /**
   * Constructor with host and port.
   */
  public DSVCalcClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). Disable TLS to avoid certificates.
        .usePlaintext()
        .build());
  }

  /**
   * Constructor with existing channel.
   */
  DSVCalcClient(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = CalculatorGrpc.newBlockingStub(channel);
    asyncStub = CalculatorGrpc.newStub(channel);
  }

  /**
   * Gracefull shutdown
   */
  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * Create AROperation object for gRPC communication.
   */
  private ArOperation fillArOperation(String operation, int a, int b) {
    ArOperation.Operation grpcArOperation;
    switch (operation) {
      case "+":
        grpcArOperation = ArOperation.Operation.PLUS;
        break;
      case "-":
        grpcArOperation = ArOperation.Operation.MINUS;
        break;
      default:
        grpcArOperation = ArOperation.Operation.UNRECOGNIZED;
        break;
    }
    return ArOperation.newBuilder().setOperation(grpcArOperation).setA(a).setB(b).build();
  }


  /**
   * Handle unary RPC compute.
   */
  public void rpcArCompute(String operation, int a, int b) {
    logger.info("Will try to compute : [ " + operation + " , " + a + " , " + b + " ]");
    ArOperation request = fillArOperation(operation, a, b);
    ArResult response;
    try {
      response = blockingStub.compute(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Result: " + response.getResult());
  }


  /**
   * Handle client-side streaming RPC compute.
   */
  public void rpcContSumCompute(ArrayList<ArOperation> arOperations) {
    // For random wait
    Random rand = new Random();
    // Print input data
    logger.info(String.format("Will try to compute sum for %d operations", arOperations.size()));

    // Create synchronization barrier (awaits() block till 0 value)
    final CountDownLatch finishLatch = new CountDownLatch(1);

    // Create Response observer - class which handle response from server
    StreamObserver<SumArResult> responseObserver = new StreamObserver<SumArResult>() {
      @Override
      public void onNext(SumArResult sumResult) {
        // There will be only one result.
        logger.info(String.format("Counted %d arithmetic operations with total sum: %f",
                      sumResult.getNum(), sumResult.getArResult().getResult()));
      }

      @Override
      public void onError(Throwable t) {
        Status status = Status.fromThrowable(t);
        logger.log(Level.WARNING, String.format("ContinuousSumCompute Failed: %s", status.toString()));
        finishLatch.countDown();  // return value already handled (it was error)
      }

      @Override
      public void onCompleted() {
        finishLatch.countDown();  // return value already handled (it was error)
      }
    };

    // Send all needed input data to stream
    StreamObserver<ArOperation> requestObserver = asyncStub.continuousSumCompute(responseObserver);
    try {
      // Send numPoints points randomly selected from the features list.
      ArOperation arop = null;
      for (int i = 0; i < arOperations.size(); ++i) {
        logger.info("Sending arithmetic operation ...");
        requestObserver.onNext(arOperations.get(i));
        // Sleep for a bit before sending the next one.
        Thread.sleep(rand.nextInt(2000) + 500);
        if (finishLatch.getCount() == 0) {
          // RPC completed or errored before we finished sending.
          // Sending further requests won't error, but they will just be thrown away.
          return;
        }
      }
    } catch (RuntimeException e) {
      logger.info("RuntimeException: when sending arithmetic operations to stream.");
      // Cancel RPC
      requestObserver.onError(e);
      throw e;
    } catch (InterruptedException ie) {
      logger.info("InterruptedException: when sleeping between sending arithmetic operation to stream.");
      // Cancel RPC
      requestObserver.onError(ie);
    }
    // Mark the end of requests
    requestObserver.onCompleted();

    // Receiving happens asynchronously
    try {
      finishLatch.await(1, TimeUnit.MINUTES);
    } catch (InterruptedException ie) {
      logger.info("InterruptedException: when waiting on async response.");
    }
  }


  /**
   * Main for the client.
   */
  public static void main(String[] args) throws Exception {
    // Access a service running on the local machine on port 50051
    DSVCalcClient client = new DSVCalcClient("localhost", 50141);
    try {
      int a = 45;
      int b = 54;
      // Use the args as the input if they are provided.
      if (args.length == 2) {
        a = Integer.valueOf(args[0]);
        b = Integer.valueOf(args[1]);
      }
      client.rpcArCompute("+", a, b);
      client.rpcArCompute("-", a, b);

      ArrayList<ArOperation> arOperations = new ArrayList();
      arOperations.add(client.fillArOperation("+", 12, 13));
      arOperations.add(client.fillArOperation("+", 34, 56));
      arOperations.add(client.fillArOperation("-", 24, 83));
      arOperations.add(client.fillArOperation("-", 46, 26));
      client.rpcContSumCompute(arOperations);
    } finally {
      client.shutdown();
    }
  }
}
