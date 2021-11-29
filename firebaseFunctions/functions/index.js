const functions = require("firebase-functions");
const express = require("express");
const cors = require("cors");
const admin = require("firebase-admin");
admin.initializeApp();


exports.getRecipeByAreaAndCategory = functions.https.onCall(async (data, context) => {

  const snapshot = await admin.firestore().collection('recipes');
  const filteredData = await snapshot.where('area','==',data.area).where('category','==',data.category).get();
  let recipes = [];
  filteredData.forEach((doc) => {
    let id = doc.id;
    let data = doc.data();
    recipes.push({ id, ...data });
  });
  
  return JSON.stringify(recipes);
});
