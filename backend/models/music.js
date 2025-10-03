import mongoose from "mongoose";

const musicSchema = new mongoose.Schema({
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
    trim: true,
  },
  origin: {
    type: String,
    required: true,
    trim: true,
  },
  instrumentsUsed: {
     type: String,
  },
  language: {
    type: String,
    required: true,
    trim: true,
  },
  description: {
    type: String,
    
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

const Music = mongoose.model("Music", musicSchema);

export default Music;
