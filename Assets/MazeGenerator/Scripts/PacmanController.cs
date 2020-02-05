using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PacmanController : MonoBehaviour
{

    public float MovementSpeed = 0f;
    private Vector3 up = Vector3.zero,
        right = new Vector3(0, 90, 0),
        down = new Vector3(0, 180, 0),
        left = new Vector3(0, 270, 0),
        currentDirection = Vector3.zero;

    private Animator animator = null;

    private bool mFloorTouched = false;
    private Rigidbody mRigidBody = null;


    private Vector3 initialPosition = Vector3.zero;
    public void Reset()
    {
        animator.SetBool("isDead", false);
        animator.SetBool("isMoving", false);
        transform.position = initialPosition;
        currentDirection = down;
    }

    // Start is called before the first frame update
    void Start()
    {
        mRigidBody = GetComponent<Rigidbody>();
        animator = GetComponent<Animator>();

        QualitySettings.vSyncCount = 0;
        initialPosition = transform.position;
        Reset();
    }

    // Update is called once per frame
    void Update()
    {
        var isMoving = true;
        if (Input.GetKey(KeyCode.UpArrow)) currentDirection = up;
        else if (Input.GetKey(KeyCode.RightArrow)) currentDirection = right;
        else if (Input.GetKey(KeyCode.DownArrow)) currentDirection = down;
        else if (Input.GetKey(KeyCode.LeftArrow)) currentDirection = left;
        else isMoving = false;

        transform.localEulerAngles = currentDirection;
        animator.SetBool("isMoving", isMoving);

        if (isMoving) transform.Translate(Vector3.forward * MovementSpeed * Time.deltaTime);



    }



}