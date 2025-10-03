import mongoose from "mongoose";

const itemSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
      trim: true,
    },
    category: {
      type: String,
      enum: ["Handicraft", "Painting", "Textile", "Jewelry", "Sculpture", "Other"  ],
      required: true,
    },
    description: {
      type: String,
      trim: true,
    },
    price: {
      type: Number,
      required: true,
      min: 0,
    },
    stock: {
      type: Number,
      default: 0,
    },
    imageUrl: {
      type: String, 
      default: "",
    },
    materialUsed: {
      type: String,
      trim: true, 
    },
    origin: {
      type: String,
      trim: true, 
    },
    isTraditional: {
      type: Boolean,
      default: true,
    },
  },
  { timestamps: true }
);

const Item = mongoose.model("Item", itemSchema);
export default Item;
