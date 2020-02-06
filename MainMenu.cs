using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class MainMenu : MonoBehaviour
{
    public void Gotoregister()
    {
        SceneManager.LoadScene(1);
        

    }

    public void GotoLogin()
    {

        SceneManager.LoadScene(2);
    }
}
