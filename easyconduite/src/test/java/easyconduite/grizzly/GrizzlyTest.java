/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package easyconduite.grizzly;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NativeFileSystem;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;
import org.apache.commons.net.telnet.TelnetClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class GrizzlyTest {

    private TelnetClient telnet;
    private InputStream in;
    private PrintStream out;
    private String prompt = "%";

    @BeforeEach
    public void setup(){
        telnet = new TelnetClient();
    }

    @Test
    public void testFTPServer(){
// Uses the current working directory as the root
        File root = new File(System.getProperty("user.dir"));

// Creates a native file system
        NativeFileSystem fs = new NativeFileSystem(root);

// Creates a noop authenticator, which allows anonymous authentication
        NoOpAuthenticator auth = new NoOpAuthenticator(fs);

// Creates the server with the authenticator
        FTPServer server = new FTPServer(auth);

// Start listening synchronously
        try {
            server.listenSync(21);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTeadminlnetClient(){
        //TelnetClient telnet = new TelnetClient();
        try {
            // Connect to the specified server
            telnet.connect("localhost", 4212);

            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            // Advance to a prompt
            readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuffer sb = new StringBuffer();
            boolean found = false;
            char ch = (char) in.read();
            while (true) {
                System.out.print(ch);
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String value) {
        try {
            out.println(value);
            out.flush();
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendCommand(String command) {
        try {
            write(command);
            return readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
