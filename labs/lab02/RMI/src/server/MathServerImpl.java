package server;

import java.rmi.*;

import compute.*;

public class MathServerImpl implements MathServer {
	
	public MathServerImpl() throws RemoteException {
		super();
		}

	@Override
	public int add(int a, int b) throws RemoteException {
		int result;
		result=a+b;
		System.out.println("Implementation.1.add: " + a + " + " + b + " = " + result);
		return result;
	}

	@Override
	public int sub(int a, int b) throws RemoteException {
		int result;
		result=a-b;
		System.out.println("Implementation.1.sub: " + a + " - " + b + " = " + result);
		return result;
	}
}