import mongoose from "mongoose";

const festivalSchema = new mongoose.Schema({
    festivalId: { type: Number, required: true, unique: true }, 
    name: { type: String, required: true },
    type: { type: String },
    religion: { type: String },
    region: { type: String },
    month: { type: String },
    specialFoods: { type: String },
    mythologicalSignificance: { type: String },
    image: { type: String } 
}, { timestamps: true });

const Festival = mongoose.model("Festival", festivalSchema);

export default Festival;
