import express from "express"
import dotenv from "dotenv"
import connectDb from "./config/db.js"
import Monument from "./models/Monument.js"
import Festival from "./models/Festival.js"
import food from "./models/food.js"
import cors from "cors"
import monumentrouter from "./routes/monument.route.js"
import festivalrouter from "./routes/festival.route.js"
import csv from 'csvtojson';
import foodrouter from "./routes/food.route.js"
import story from "./models/story.js"
import storyrouter from "./routes/story.route.js"
import axios from "axios";





const url = `https://sanskriti-p2v9.onrender.com`;
const interval = 30000;

function reloadWebsite() {
  axios
    .get(url)
    .then((response) => {
      console.log("website reloded");
    })
    .catch((error) => {
      console.error(`Error : ${error.message}`);
    });
}

setInterval(reloadWebsite, interval);





dotenv.config()

const app=express()



const port=process.env.PORT || 5000
app.use(cors());
app.use(express.json())
app.use("/api/monument", monumentrouter)
app.use("/api/festival", festivalrouter)
app.use("/api/food", foodrouter)
app.use("/api/story", storyrouter)





const startServer = async () => {
  await connectDb();

  app.listen(port, () => {
    console.log(`🚀 Server started on port: ${port}`);
  });
};

startServer();


