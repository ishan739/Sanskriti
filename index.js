import express from "express"
import dotenv from "dotenv"
import connectDb from "./config/db.js"
import Monument from "./models/Monument.js"
import cors from "cors"
import monumentrouter from "./routes/monument.route.js"



dotenv.config()

const app=express()



const port=process.env.PORT || 5000
app.use(cors());
app.use(express.json())
app.use("/api/monument", monumentrouter)





const startServer = async () => {
  await connectDb();

  app.listen(port, () => {
    console.log(`🚀 Server started on port: ${port}`);
  });
};

startServer();


