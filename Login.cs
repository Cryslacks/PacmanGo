using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System.Net;
using System.Net.Sockets;
using System.IO;





public class Login : MonoBehaviour
{
    private StreamReader _read;
    private StreamWriter _write;


    public InputField nameField;
    public InputField passwordField;

    public Button submit;
    private Socket _clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
    private byte[] _recieveBuffer = new byte[8142];

    private string path;
    private string jsonString;

  
    
    

    public void CallLogin()
    {
        /*

        //Get hwid from phone

        IDevice device = DependencyService.Get<IDevice>();
        string hwid;

        if (device != null)
        {
            hwid = device.GetID();
        }
        */
        
         
        Create myObject = new Create();
        myObject.protocol = "LOGIN_USER";
        myObject.data = new string[] { nameField.text, passwordField.text};


        jsonString = JsonUtility.ToJson(myObject);

        path = Application.persistentDataPath + "/Login.json";
        File.WriteAllText(path, jsonString);
        System.IO.File.WriteAllText(path, jsonString);

        Debug.Log(jsonString);


        // Jäg är har
        //StartCoroutine(Register());




        
        try
        {
            _clientSocket.Connect(new IPEndPoint(IPAddress.Loopback, 16001));  //Ni kan ändra port 16001, det är local host.  

        }
        catch (SocketException ex)
        {
            Debug.Log(ex.Message);
        }

    }


    public void VerifyInput()
    {

        int a = nameField.text.Length;
        int b = passwordField.text.Length;

        submit.interactable = (a >= 8 && b >= 8);
    }


    [System.Serializable]
    public class Create
    {
        public string protocol;
        public string[] data;

    }


    



    /*

    public interface IDevice
    {
        string GetID();

    }


    [assembly: Xamarin.Forms.Dependency(typeof(AndroidDevice))]

    public class AndroidDevice : IDevice
    {

        public string GetID()
        {
            return Android.Provider.Settings.Secure.GetString(Android.App.Application.Context.ContentResolver, Android.Provider.Settings.Secure.AndroidId);
        }
    }


    // Kolla desierialize Login User Request return 
    // Kolla desierialize Create User Request return 
    */

}
