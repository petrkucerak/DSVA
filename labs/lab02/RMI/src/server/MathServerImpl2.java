package server;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import compute.*;

public class MathServerImpl2 extends UnicastRemoteObject implements MathServer {
	
	public MathServerImpl2() throws RemoteException {
		super();
		}

	@Override
	public int add(int a, int b) throws RemoteException {
		int result;
		result=a+b;
		System.out.println("Implementation.2.add: " + a + " + " + b + " = " + result);
		return result;
	}

	@Override
	public int sub(int a, int b) throws RemoteException {
		int result;
		result=a-b;
		System.out.println("Implementation.2.sub: " + a + " - " + b + " = " + result);
		return result;
	}

	@Override
	public int mul(int a, int b) throws RemoteException {
		int result;
		result = a * b;
		System.out.println("Implementation.2.mul: " + a + " * " + b + " = " + result);
		return result;
	}

	@Override
	public int div(int a, int b) throws RemoteException{
		if (b == 0){
			System.err.println("Division by 0 isn't possible!");
			return 0;
		}
		int result;
		result = a / b;
		System.out.println("Implementation.2.div: " + a + " / " + b + " = " + result);
		return result;
	}

	@Override
	public float divF(float a, float b) throws RemoteException{
		float result;
		result = a / b;
		System.out.println("Implementation.2.divF: " + a + " / " + b + " = " + result);
		return result;
	}
}