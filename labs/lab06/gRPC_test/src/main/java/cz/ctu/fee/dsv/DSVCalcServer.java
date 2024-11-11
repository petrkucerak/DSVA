package cz.ctu.fee.dsv;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSVCalcServer {
    private static final Logger logger = Logger.getLogger(cz.ctu.fee.dsv.DSVCalcServer.class.getName());

    private Server server;

    /**
     * Starts the server.
     */
    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50141;
        server = ServerBuilder.forPort(port)
                .addService(new cz.ctu.fee.dsv.DSVCalcServer.CalcImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                cz.ctu.fee.dsv.DSVCalcServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    /**
     *  Stops the server.
      */
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final cz.ctu.fee.dsv.DSVCalcServer server = new cz.ctu.fee.dsv.DSVCalcServer();
        server.start();
        server.blockUntilShutdown();
    }


    /**
     * Implementation of the service.
     */
    static class CalcImpl extends CalculatorGrpc.CalculatorImplBase {

        /**
         * Method for computing Arithmetic operation.
         */
        private double computeArOperation(ArOperation arop) {
            switch (arop.getOperation()) {
                case PLUS:
                    logger.info(String.format("Computing: %f + %f ...", arop.getA(), arop.getB()));
                    return (arop.getA() + arop.getB());
                case MINUS:
                    logger.info(String.format("Computing: %f - %f ...", arop.getA(), arop.getB()));
                    return (arop.getA() - arop.getB());
                case UNRECOGNIZED:
                default:
                    return (0.0);
            }
        }

        /**
         * Implementation of unary RPC method.
         * @param arop
         * @param responseObserver
         */
        @Override
        public void compute(ArOperation arop,
                        io.grpc.stub.StreamObserver<ArResult> responseObserver) {
            // Create response
            ArResult reply = ArResult.newBuilder().setResult(computeArOperation(arop)).build();
            // Send it to channel
            responseObserver.onNext(reply);
            // Close channel
            responseObserver.onCompleted();
        }


        /**
         * Implementation of client-side streaming RPC method.
         * @param responseObserver
         * @return
         */
        public StreamObserver<ArOperation> continuousSumCompute(final StreamObserver<SumArResult> responseObserver){
            // Define and create StreamObserver, which handles stream from client
            return new StreamObserver<ArOperation>() {
                int numOfOperations = 0;
                double tmpSum = 0;

                @Override
                public void onNext(ArOperation operation) {
                    numOfOperations++;
                    tmpSum += computeArOperation(operation);
                }

                @Override
                public void onError(Throwable t) {
                    logger.log( Level.WARNING, "Encountered error in continuousSumCompute", t);
                }

                @Override
                public void onCompleted() {
                    logger.info("Completed called ...");
                    responseObserver.onNext( SumArResult.newBuilder().
                            setNum(numOfOperations).
                            setArResult(ArResult.newBuilder().setResult(tmpSum).build()).
                            build() );
                    responseObserver.onCompleted();
                }
            };
        }

    }
}

