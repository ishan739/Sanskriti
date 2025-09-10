import mongoose from "mongoose";

const artSchema = new mongoose.Schema({
  id: {
    type: Number,
    required: true,
    unique: true,
    index: true, 
  },
  name: {
    type: String,
    required: true,
    trim: true,
  },
  type: {
    type: String,
    required: true,
  },
  origin: {
    type: String,
    required: true,
  },
  materialused: {
    type: String,
    
  },
  description: {
    type: String,
    required: true,
  },
  imageurl: {
    type: String,
    default: "",
  },
  wikiurl: {
    type: String,
    default: "",
  },
}, { timestamps: true });

const Art = mongoose.model("Art", artSchema);

export default Art;
