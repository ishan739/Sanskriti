import multer from "multer";


const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, "./public");
  },
  filename: (req, file, cb) => {
    cb(null, Date.now() + "-" + file.originalname);
  },
});


export const uploadImage = multer({
  storage,
  fileFilter: (req, file, cb) => {
    if (
      file.mimetype.startsWith("image/")
    ) {
      cb(null, true);
    } else {
      cb(new Error("Only image files are allowed!"), false);
    }
  },
});


export const uploadAudio = multer({
  storage,
  fileFilter: (req, file, cb) => {
    if (
      file.mimetype === "audio/mpeg" ||
      file.mimetype === "audio/mp3"
    ) {
      cb(null, true);
    } else {
      cb(new Error("Only mp3 files are allowed!"), false);
    }
  },
});


