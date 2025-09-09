import express from "express"
import dotenv from "dotenv"
import connectDb from "./config/db.js"
import Monument from "./models/Monument.js"
import Festival from "./models/Festival.js"
import cors from "cors"
import monumentrouter from "./routes/monument.route.js"
import festivalrouter from "./routes/festival.route.js"




dotenv.config()

const app=express()



const port=process.env.PORT || 5000
app.use(cors());
app.use(express.json())
app.use("/api/monument", monumentrouter)
app.use("/api/festival", festivalrouter)





const startServer = async () => {
  await connectDb();

  app.listen(port, () => {
    console.log(`🚀 Server started on port: ${port}`);
  });
};

startServer();



