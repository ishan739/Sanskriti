import express from 'express';
import { getMonumentById, getMonumentsByDistrict, uploadCoverImage, uploadFirstImage, uploadSecondImage, uploadThirdImage } from '../controllers/Monument.controller.js';
import { upload } from '../middlewares/multer.js';

const monumentrouter = express.Router();


monumentrouter.get('/:id', getMonumentById);


monumentrouter.get('/district/:district', getMonumentsByDistrict);
monumentrouter.post('/:id/upload-cover', upload.single('image'), uploadCoverImage);
monumentrouter.post('/:id/upload-fimage', upload.single('image'), uploadFirstImage);
monumentrouter.post('/:id/upload-simage', upload.single('image'), uploadSecondImage);
monumentrouter.post('/:id/upload-timage', upload.single('image'), uploadThirdImage);

export default monumentrouter;
