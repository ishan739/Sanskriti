import mongoose from "mongoose";

const postSchema = new mongoose.Schema({
    author: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    media: {
        type: String,
        required: true
    },
    caption:{
        type:String
    },
    location:{
        type:String
    },
    likes:[
        {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        }
    ],
    comments:[
        {
        author:{
        type: mongoose.Schema.Types.ObjectId,
        ref: "User"},
        message:{
            type:String
        }
        }
    ]

}, { timestamps: true })

const Post = mongoose.model("Post",postSchema)
export default Post