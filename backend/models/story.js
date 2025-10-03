import mongoose from "mongoose";

const storySchema = new mongoose.Schema({
    id: {
    type: Number,
    required: true,
    unique: true,
    index: true
  },
  name: {
    type: String,
    required: true,
    trim: true 
  },
  title: {
    type: String,
    required: true,
    trim: true 
  },
  category: {
    type: String,
    required: true,
    enum: ["Epic", "Mythology", "Legends", "Folk Tale"],
    index: true
  },
  story: {
    type: String,
    required: true,
  },
   audiourl: {
    type: String,
    
    default: ""
  },
  thumbnail: {
    type: String,
    default: ""
  }
}, { timestamps: true });

const story= mongoose.model("Story", storySchema);

export default story;