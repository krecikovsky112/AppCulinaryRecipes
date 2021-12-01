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

exports.getRecipeByindigrients = functions.https.onCall(async (data, context) => {

  let checker = (arr, target) => target.every(v => arr.includes(v));
  const filteredData = await admin.firestore().collection('recipes').get();

  let recipes = [];
  filteredData.forEach((doc) => {
      let id = doc.id;
      let dataRecipe = doc.data();
      if (checker(dataRecipe.indigrients, data.ingredients)) {
          recipes.push({
              id,
              ...dataRecipe
          });
      }
  });

  return JSON.stringify(recipes);
});