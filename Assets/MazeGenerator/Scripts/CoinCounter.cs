using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CoinCounter : MonoBehaviour
{
    public GameObject coinText;
    public static int coinAmount = 0;    

    // Update is called once per frame
    void Update()
    {
        coinText.GetComponent<Text>().text = "Coins: "+ (coinAmount).ToString();
     }
}
