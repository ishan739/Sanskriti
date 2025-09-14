import Monument from "../models/Monument.js";
import uploadOnCloudinary from "../config/cloudinary.js";

export const getMonumentById = async (req, res) => {
    try {
        const { id } = req.params;
        const monument = await Monument.findOne({ id: Number(id) });

        if (!monument) {
            return res.status(404).json({ message: 'Monument not found' });
        }

        res.status(200).json(monument);
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};


export const getMonumentsByDistrict = async (req, res) => {
    try {
        const { district } = req.params;
        const monuments = await Monument.find({ district: district });

        if (!monuments || monuments.length === 0) {
            return res.status(404).json({ message: 'No monuments found in this district' });
        }

        res.status(200).json(monuments);
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};

export const uploadCoverImage = async (req, res) => {
    try {
        const { id } = req.params;
        if (!req.file) {
            return res.status(400).json({ message: 'No file uploaded' });
        }

        const imageUrl = await uploadOnCloudinary(req.file.path);

        const monument = await Monument.findOneAndUpdate(
            { id: Number(id) },
            { cover: imageUrl },
            { new: true }
        );

        if (!monument) {
            return res.status(404).json({ message: 'Monument not found' });
        }

        res.status(200).json({ message: 'Cover image uploaded successfully', monument });
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};


export const uploadFirstImage = async (req, res) => {
    try {
        const { id } = req.params;
        if (!req.file) {
            return res.status(400).json({ message: 'No file uploaded' });
        }

        const imageUrl = await uploadOnCloudinary(req.file.path);

        const monument = await Monument.findOneAndUpdate(
            { id: Number(id) },
            { furl: imageUrl },
            { new: true }
        );

        if (!monument) {
            return res.status(404).json({ message: 'Monument not found' });
        }

        res.status(200).json({ message: 'First image uploaded successfully', monument });
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};

export const uploadSecondImage = async (req, res) => {
    try {
        const { id } = req.params;
        if (!req.file) {
            return res.status(400).json({ message: 'No file uploaded' });
        }

        const imageUrl = await uploadOnCloudinary(req.file.path);

        const monument = await Monument.findOneAndUpdate(
            { id: Number(id) },
            { surl: imageUrl },
            { new: true }
        );

        if (!monument) {
            return res.status(404).json({ message: 'Monument not found' });
        }

        res.status(200).json({ message: 'Second image uploaded successfully', monument });
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};

export const uploadThirdImage = async (req, res) => {
    try {
        const { id } = req.params;
        if (!req.file) {
            return res.status(400).json({ message: 'No file uploaded' });
        }

        const imageUrl = await uploadOnCloudinary(req.file.path);

        const monument = await Monument.findOneAndUpdate(
            { id: Number(id) },
            { turl: imageUrl },
            { new: true }
        );

        if (!monument) {
            return res.status(404).json({ message: 'Monument not found' });
        }

        res.status(200).json({ message: 'Third image uploaded successfully', monument });
    } catch (error) {
        res.status(500).json({ message: 'Server Error', error: error.message });
    }
};


export const updateMonument = async (req, res) => {
    try {
        const { id } = req.params;
        const updateData = req.body; 

        
        if (!updateData || Object.keys(updateData).length === 0) {
            return res.status(400).json({ message: "No update data provided" });
        }

        const monument = await Monument.findOneAndUpdate(
            { id: Number(id) },
            { $set: updateData },
            { new: true, runValidators: true }
        );

        if (!monument) {
            return res.status(404).json({ message: "Monument not found" });
        }

        res.status(200).json({
            message: "Monument updated successfully",
            monument
        });
    } catch (error) {
        res.status(500).json({
            message: "Server Error",
            error: error.message
        });
    }
};



