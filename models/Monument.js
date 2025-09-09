import mongoose from 'mongoose';

const monumentSchema = new mongoose.Schema({
    id: { type: Number, required: true, unique: true },
    name: { type: String, required: true },
    location: { type: String, required: true },
    district: { type: String, required: true },
    description: { type: String, required: true },
    cover: { type: String },
    furl: { type: String },
    surl: { type: String },
    turl: { type: String }
}, { timestamps: true });

const Monument = mongoose.model('Monument', monumentSchema);

export default Monument;
