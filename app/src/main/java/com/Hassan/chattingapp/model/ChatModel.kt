package com.Hassan.chattingapp.model

class ChatModel(var senderId:String,var message:String){
    //här ska vi ha en konstruktor som tar emot två strängar
    //och sätter dem till variablerna
    constructor():this("","")
}
