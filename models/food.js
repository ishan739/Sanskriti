import mongoose from "mongoose";


const foodSchema = new mongoose.Schema({
    id: {
        type: Number, required: true, unique: true 
    },
    name: {
        type: String,
        required: true,
        trim: true
    },
    type: {
        type: String,
        required: true,
        trim: true,

    },
    region: {
        type: String,
        trim: true
    },
    origin: {
        type: String,
        trim: true
    },
    mainIngredients: {
        type: String,
    },
    description: {
        type: String,
        default: "",
        trim: true
    },
    imageurl: {
        type: String,
        default: ""
    }
}, {
    timestamps: true
});

const food = mongoose.model('food', foodSchema);


export default food;