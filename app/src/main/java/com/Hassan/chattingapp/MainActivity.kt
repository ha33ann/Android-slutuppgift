package com.Hassan.chattingapp

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.Hassan.chattingapp.adapter.ChatAdapter
import com.Hassan.chattingapp.base.SplashActivity
import com.Hassan.chattingapp.databinding.ActivityMainBinding
import com.Hassan.chattingapp.model.ChatModel
import com.Hassan.chattingapp.utils.ConstantKeys
import com.Hassan.chattingapp.utils.SpeakListener
import com.Hassan.chattingapp.utils.TinyDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import org.intellij.lang.annotations.Language
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    //instansierar databasen och autentiseringen för firebase
    //och skapar en instans av tinyDB för att spara data lokalt
    lateinit var binding: ActivityMainBinding
    lateinit var chatAdapter: ChatAdapter
    lateinit var databaseReference: DatabaseReference
    lateinit var senderRoom: String
    lateinit var recieverRoom: String
    lateinit var tintDb: TinyDB
    lateinit var array: ArrayList<ChatModel>
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var tts: TextToSpeech? = null








    override fun onCreate(savedInstanceState: Bundle?) {
        //sätter layouten till activity_main
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        databaseReference = FirebaseDatabase.getInstance().getReference("chats")
        array = ArrayList();
        tintDb = TinyDB(this)
        tts = TextToSpeech(this, this)
        senderRoom = FirebaseAuth.getInstance().uid + ConstantKeys.USER_ID
        recieverRoom = ConstantKeys.USER_ID + FirebaseAuth.getInstance().uid
        binding.msgEdt.addTextChangedListener {
            if (!binding.msgEdt.text.trim().toString().equals("")) {
                binding.micImg.visibility = View.GONE
                binding.sendImg.visibility = View.VISIBLE
            } else {
                binding.micImg.visibility = View.VISIBLE
                binding.sendImg.visibility = View.GONE
            }


        }



        //om användaren trycker på sendImg,
        //skicka meddelandet till databasen och rensa edittexten
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                array.clear()
                //hämtar alla meddelanden från databasen
                //och lägger till dem i arrayen
                for (dataSnapshot: DataSnapshot in snapshot.children) {

                    var chatModel: ChatModel = dataSnapshot.getValue<ChatModel>()!!
                    array.add(ChatModel(chatModel.senderId, chatModel.message))

                }
                //skapar en instans av chatAdapter
                //och sätter den till recyclerviewn
                val adapter = ChatAdapter(this@MainActivity, array, tintDb, object : SpeakListener {
                    override fun click(message: String) {
                        speakOut(message)
                    }
                })
                //sätter layoutmanager till recyclerviewn
                val mLayoutManager = LinearLayoutManager(this@MainActivity)
                mLayoutManager.stackFromEnd = true

                binding.recyclerView.layoutManager = mLayoutManager
                binding.recyclerView.adapter = adapter
            }

            //om det inte går att hämta meddelanden från databasen,
            //skriv ut ett felmeddelande
            override fun onCancelled(error: DatabaseError) {
                Log.w("wow123", "Failed to read value.", error.toException())
            }
        })
        clicks()
    }


    private fun clicks() {
        //om användaren trycker på sendImg,
        //skapa en onClickListener och skicka meddelandet till databasen
        //och rensa edittexten
        binding.sendImg.setOnClickListener {
            if (!binding.msgEdt.text.trim().toString().equals("")) {
                var chatmodel: ChatModel = ChatModel(tintDb.getString(ConstantKeys.USER_ID),
                    binding.msgEdt.text.trim().toString())
                databaseReference.child(databaseReference.push().key.toString()).setValue(chatmodel)
                    .addOnCompleteListener {
                        binding.msgEdt.text = null
                    }

            } else {

            }



        }





        //om användaren trycker på buttonrestart,
        //skapa en onClickListener och rensa databasen där chatten finns
        binding.buttonrestart.setOnClickListener {
            databaseReference.removeValue()
        }
        //om användaren trycker på buttonlogout,
        //skapa en onClickListener och logga ut användaren
        //och ta användaren tillbaka till start-sidan
        binding.logoutImg.setOnClickListener {
            tintDb.putString(ConstantKeys.USER_ID, "")
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
        //om användaren trycker på micImg,
        //skapa en onClickListener och starta en intent
        //för att få användarens röst
        binding.micImg.setOnClickListener {

            //under denna rad anropar vi speech recognizer intent.
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)


            //under denna rad skickar vi språkmodellen
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )


            //under denna rad skickar vi vårt språk som standard
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )


            //under denna rad anger vi ett promptmeddelande
            //där vi ber användaren att tala något.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")


            //här skapar vi en try catch block som kollar
            //om det går att starta en intent
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                //om catch block körs, skriv ut ett felmeddelande
                Toast
                    .makeText(
                        this@MainActivity, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    //om det går att starta intenten,
    //skapa en onActivityResult
    //och hämta meddelandet från intenten och skicka det till databasen
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            //om det inte går att använda text-to-speech,
            //skriv ut ett felmeddelande
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    //under denna rad skapar vi en onActivityResult
    //och hämtar meddelandet från intenten och skickar det till databasen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            //under denna rad kollar vi om resultkoden är ok
            if (resultCode == RESULT_OK && data != null) {


                //om det är ok,
                //hämta meddelandet från intenten
                //och skicka det till databasen
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>


                // under denna rad sätter vi meddelandet i edittexten
                binding.msgEdt.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }

    private fun speakOut(text: String) {
        //om det går att sätta språket,
        //skapa en speakOut och sätt texten till meddelandet
        //som användaren skickar
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    public override fun onDestroy() {
        //om appen stängs ner, skapa en onDestroy som stänger texttospeech
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}