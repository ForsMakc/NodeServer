package student.bazhin;

import student.bazhin.pocket.PocketData;
import student.bazhin.pocket.PocketHeaders;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        new Core().perform();
    }

}
