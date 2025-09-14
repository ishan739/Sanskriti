import mongoose from "mongoose";

const cartSchema = new mongoose.Schema(
  {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    items: [
      {
        item: {
          type: mongoose.Schema.Types.ObjectId,
          ref: "Item",
          required: true,
        },
        quantity: {
          type: Number,
          default: 1,
          min: 1,
        },
        priceAtPurchase: {
          type: Number,
          required: true, 
        },
      },
    ],
    totalAmount: {
      type: Number,
      default: 0,
    },
    status: {
      type: String,
      enum: ["Pending", "Ordered", "Cancelled"],
      default: "Pending",
    },
  },
  { timestamps: true }
);

const Cart = mongoose.model("Cart", cartSchema);
export default Cart;
