using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CollectCoin : MonoBehaviour
{
   
    void OnTriggerEnter(Collider other)
    {
            if (gameObject.tag.Equals("Coin"))
            {
                CoinCounter.coinAmount++;
            }
    
        Destroy(gameObject);
    }
}
