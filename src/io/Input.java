package io;

import java.io.Serializable;
import java.util.Scanner;

public final class Input implements Serializable {

    private Input() {}

    public static String reader(String message) {
        if (message != null) {
            Output.write(message + " ", false);
        }
        return new Scanner(System.in).nextLine();
    }

}
