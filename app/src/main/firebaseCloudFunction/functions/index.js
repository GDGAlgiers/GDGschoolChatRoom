// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// a function triggerd when a new user account is created, 
// where we will create a user document in the Firebase firestore
exports.addNewUserToStore = functions.auth.user().onCreate(event =>{
    const email = event.data.email;
    const uid = event.data.uid;
    if(!email) return;
    var username =  username = email.split("@")[0];
    console.log("user name is "+ username+" and his or her mail mail is "+email);
    return admin.firestore().collection("users").doc(uid).set({
        email,
        username,
    })
});

/**
 * a function used to foramt a new message added to the real time database 
 * it adds the sender name and replcae some special words with emojis 
 */
exports.formatMessage = functions.database.ref("/messages/{id}").onCreate((event =>{
    const message  = event.data.val();
    let formattedMessage = message.text.replace(/:mdr:/gi, "😂")
                                       .replace(/:em:/gi,"😑")
                                       .replace(/:waw:/gi,"😮")
                                       .replace(/:love:/gi,"😍")
    const updates={
        "text": message.user.name+": "+formattedMessage
    }
    return event.data.ref.update(updates);
}))