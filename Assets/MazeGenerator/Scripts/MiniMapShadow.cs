using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MiniMapShadow : MonoBehaviour
{
    public float storedShadowDistance;


    public void OnPreRender() {
        storedShadowDistance = QualitySettings.shadowDistance;
        QualitySettings.shadowDistance = 0;
    }


    public void OnPostRender()
    {
        QualitySettings.shadowDistance = storedShadowDistance;
    }
}