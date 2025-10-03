import express from 'express';
import { getMonumentById, getMonumentsByDistrict, updateMonument, uploadCoverImage, uploadFirstImage, uploadSecondImage, uploadThirdImage } from '../controllers/Monument.controller.js';
import { uploadImage } from '../middlewares/multer.js';

const monumentrouter = express.Router();


monumentrouter.get('/:id', getMonumentById);


monumentrouter.get('/district/:district', getMonumentsByDistrict);
monumentrouter.post('/:id/upload-cover', uploadImage.single('image'), uploadCoverImage);
monumentrouter.post('/:id/upload-fimage', uploadImage.single('image'), uploadFirstImage);
monumentrouter.post('/:id/upload-simage', uploadImage.single('image'), uploadSecondImage);
monumentrouter.post('/:id/upload-timage', uploadImage.single('image'), uploadThirdImage);
monumentrouter.patch("/:id", updateMonument);

export default monumentrouter;
