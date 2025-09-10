import mongoose from 'mongoose';


const danceSchema = new mongoose.Schema({
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
  type: {
    type: String,
    trim: true,
    
  },
  origin: {
    type: String,
    trim: true
  },
  description: {
    type: String,
    trim: true
  },
  imageurl: {
    type: String,
    default: ""
    
  },
  videourl: {
    type: String,
    default: ""
    
  },
  wikiurl: {
    type: String,
    trim: true,
    
  }
}, { timestamps: true });




const Dance = mongoose.model('Dance', danceSchema);
export default Dance;

