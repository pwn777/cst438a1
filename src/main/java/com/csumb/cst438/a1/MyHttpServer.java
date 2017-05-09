package com.csumb.cst438.a1;  
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.lang.Character;

//Modified by Sam Roy

public class MyHttpServer {

	static final String RESOURCE_DIR = "src/main/resources/";
	static final int PORT = 8080;
        static Random generator = new Random();

	public static void main(String[] args) {

		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
			System.out.print("Server started. ");
			// Display current directory
			System.out.print("pwd=" + System.getProperty("user.dir"));
			server.createContext("/", new MyHandler());
			server.setExecutor(null); // creates a default executor
			server.start();
			System.out.println();
			System.out.println("MyHttp Server running.");
		} catch (Exception e) {
			System.out.println("My HttpServer Exception: " + e.getMessage());
		}
	}

	public static class MyHandler implements HttpHandler {
            
                private Game game = new Game();
                private String cookie="0";     // used to keep track of current game 

                /**
                 * handle an HTTP request
                 * @param t
                 * @throws IOException 
                 */
		public void handle(HttpExchange t) throws IOException {
			String uri = t.getRequestURI().toString();
			System.out.println("URI=" + uri);
			if (uri.endsWith(".gif") || uri.endsWith(".ico")) {
				// http get request for an image file
				sendFile(t, uri.substring(1));
			} else {
				// come here to play the game
                                String response = "";
                                // get cookie value from http request
                                String requestCookie = t.getRequestHeaders().getFirst("Cookie");
                                System.out.println("Cookie=" + requestCookie);
                                // if there is no cookie, or it is "0" or differfent from the current value, then start a new game
                                if (requestCookie==null || requestCookie.equals("0") || cookie.equals("0") || !requestCookie.equals(cookie)) {
                                    game.startNewGame();
                                    cookie=generateCookie();    
                                    response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                            + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                            + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                            + "<form action=\"/\" method=\"get\"> "
                                            + "Guess a character <input type=\"text\" maxlength=\"1\" name=\"guess\"><br>"
                                            + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                } else {
                                    // continue with current game
                                    // Make all characters lower case, so the user can still enter Uppercase
                                    char ch = Character.toLowerCase(uri.charAt(uri.length()-1));  // letter that user has guessed
       
                                    // Define numCharacters after the "=" sign in the URI
                                    int numChars = (uri.substring(uri.lastIndexOf('=') + 1)).length();
                                    
                                    // If the userinput matches the regex a-z, upper and lower and also there is only 1 character input in URI after = sign
                                    // Then continue the main program
                                    if(Character.toString(ch).matches("^[a-zA-Z]*$") && (numChars == 1))
                                    {
                                        int result = game.playGame(ch);
                                        switch(result) {
                                            case 0: // good guess, continue game
                                                response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                                + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                                + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                                + "<form action=\"/\" method=\"get\"> "
                                                + "Guess a character <input type=\"text\" maxlength=\"1\" name=\"guess\"><br>"
                                                + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                                break;
                                            case 1: // good guess, win game
                                                 response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                                + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                                + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + "</h2>"
                                                + "<h2>Congratulations you win!</h2>" + "</body></html>";
                                                 cookie="0";
                                                 break;
                                            case 2: // bad guess, continue game
                                                response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                                + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                                + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                                + "<form action=\"/\" method=\"get\"> "
                                                + "Guess a character <input type=\"text\" maxlength=\"1\" name=\"guess\"><br>"
                                                + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                                break;
                                            case 3: // bad guess, lost game
                                                 response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                                + "<img src=\"" + "h7.gif" + "\">" + "<h2>You lost!  The word is " + game.getWord() + "</h2>"
                                                + "</body></html>";
                                                 cookie="0";
                                                 break;
                                        }

                                    }
                                    // Else show game state, put "Invalid input"
                                    else
                                    {
                                        response = "<!DOCTYPE html><html><head><title>MyHttpServer</title></head><body><h2>Hangman</h2>"
                                                + "<img src=\"" + "h" + game.getState() + ".gif" + "\">"
                                                + "<h2 style=\"font-family:'Lucida Console', monospace\"> " + game.getDisplayWord() + "</h2>"
                                                + "<form action=\"/\" method=\"get\"> "
                                                + "Invalid Input! Guess again: <input type=\"text\" maxlength=\"1\" name=\"guess\"><br>"
                                                + "<input type=\"submit\" value=\"Submit\">" + "</form></body></html>";
                                    }
                                }
                                t.getResponseHeaders().set("Content-Type", "text/html");
                                if (cookie!=null) 
                                     t.getResponseHeaders().set("Set-Cookie", cookie);
                                else 
                                     t.getResponseHeaders().set("Set-Cookie", "0");
                                System.out.println("New cookie:" + cookie);
                                t.sendResponseHeaders(200, response.length());
                                System.out.println("response=" + response);
                                OutputStream os = t.getResponseBody();
                                os.write(response.getBytes());
                                os.close();
                         }
                }

                /*
                 * send a gif file
                 */
                private static void sendFile(HttpExchange t, String filename) {

                        try {
                                File file = new File(RESOURCE_DIR + filename);
                                byte[] fileData = new byte[(int) file.length()];
                                DataInputStream dis = new DataInputStream(new FileInputStream(file));
                                dis.readFully(fileData);
                                dis.close();

                                t.getResponseHeaders().set("Content-Type", "image/gif");
                                t.sendResponseHeaders(200, fileData.length);
                                OutputStream os = t.getResponseBody();
                                os.write(fileData);
                                os.close();

                        } catch (Exception e) {
                                System.out.println("Error in sendFile:" + filename + " " + e.getMessage());
                        }

                }
        
                /*
                * generate a random cookie which is a random long integer
                */
                 private String generateCookie() {
                    return Long.toString(generator.nextLong());
                 }
                 
        }  // end of static class MyHandler
        
}
