




using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System;
using System.Threading;
using System.Text;




public class Registration : MonoBehaviour

{

    public InputField nameField;    // Namn man vill registrera 
    public InputField passwordField; // Lösenord man vill registrera 
    public InputField confirmField;
    public Button submit;           // Registrerings knapp 


    //private static String respone = String.Empty();

    private string jsonString;
    public string hwid; //hardware id

    private int port = 16601;

    private IPEndPoint toserver;

    public static StringBuilder sb = new StringBuilder();

    public const int BufferSize = 256;
 
    public byte[] buffer = new byte[BufferSize];
  
  
    
    // Skickar signaler till threads, 
    private static ManualResetEvent connectDone =
  new ManualResetEvent(false);
    private static ManualResetEvent sendDone =
        new ManualResetEvent(false);
    private static ManualResetEvent receiveDone =
        new ManualResetEvent(false);

    
    void Start()
    {
        Debug.Log(SystemInfo.deviceUniqueIdentifier);
        hwid = SystemInfo.deviceUniqueIdentifier;
    }
    
    public void CallRegister()
    {


        Create myObject = new Create();
        myObject.protocol = "CREATE_USER";
        myObject.data = new string[] { nameField.text, passwordField.text, hwid };

        jsonString = JsonUtility.ToJson(myObject);

        try
        {

            toserver = new IPEndPoint(IPAddress.Loopback, 16001);

            Socket client = new Socket(AddressFamily.InterNetwork,
                    SocketType.Stream, ProtocolType.Tcp);

            client.BeginConnect(toserver, new AsyncCallback(CallConnect), client);

            connectDone.WaitOne();  // waiting blocking
            Send(client, jsonString);

            sendDone.WaitOne();
            // Release the socket.  
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }
        catch (Exception e)
        {


        }

        Debug.Log(jsonString);

    }


    // Verifierar namn och lösenord
    public void VerifyInput()
    {

        int a = nameField.text.Length;
        int b = passwordField.text.Length;
        
        submit.interactable = (a >= 8 && b >= 8 && passwordField.text == confirmField.text);
    }




    // Protokol objekt serializable
    [System.Serializable]
    public class Create
    {
        public string protocol;
        public string[] data;

    }


    private void CallConnect(IAsyncResult ar)
    {
        try
        {
            // Hämtar socketen 
            Socket client = (Socket)ar.AsyncState;

            // Avslutar anslutningen 
            client.EndConnect(ar);

            //Signalerar att anslutningen har hänt
            connectDone.Set();
        }
        catch (Exception e)
        {

        }
    }

    private void Send(Socket client, String p)
    {
        //Konverterar sträng data till sträng data. 
        byte[] byteData = System.Text.Encoding.ASCII.GetBytes(p);


        // Börja skicka data till servern
        client.BeginSend(byteData, 0, byteData.Length, 0,
            new AsyncCallback(SendCallback), client);
    }
    private static void SendCallback(IAsyncResult ar)
    {
        try
        {
            //Socket för att ansluta till servern
            Socket client = (Socket)ar.AsyncState;

            //Signalerar att data har skickats.
            sendDone.Set();
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
        }
    }
    /*
    private static void recive(Socket client)
    {
        try
        {
            toserver = client;

            client.BeginReceive(buffer, 0, BufferSize, 0,
                    new AsyncCallback(ReceiveCallback), toserver);
        }
        catch (Exception e)
        {
            Debug.Log("HEJ");
        }
    }
    private static void recivecall(IAsyncResult ar)
    {
        try
        {
            Socket client = (Socket)ar.AsyncState;

            int readbyte = client.EndReceive(ar);
            if (readbyte > 0)
            {
                sb.Append(Encoding.ASCII.GetString(buffer, 0, readbyte));

                client.BeginReceive(buffer, 0, BufferSize, 0,
                        new AsyncCallback(ReceiveCallback), toserver);
            }
            if (sb.Length > 0)
            {

                respone = sb.ToString();

            }
        }
        catch(Exception e)
        {
            Debug.Log("Hej");
        }

       
    }
    */
}
