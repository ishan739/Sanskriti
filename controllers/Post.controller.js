import uploadOnCloudinary from "../config/cloudinary.js";
import Post from "../models/post.js";
import User from "../models/user.js";


export const uploadPost = async (req, res) => {
  try {
    const { caption, location } = req.body;
    const userId = req.userId || (req.user && req.user._id);
    if (!userId) return res.status(401).json({ message: "Not authorized" });

    if (!req.file) {
      return res.status(400).json({ message: "media is required" });
    }

    const media = await uploadOnCloudinary(req.file.path);

    const post = await Post.create({
      caption,
      media,
      location,
      author: userId,
    });

    const user = await User.findById(userId);
    user.posts = user.posts || []; 
    user.posts.push(post._id);
    await user.save();

    const populatedPost = await Post.findById(post._id)
      .populate("author", "name profileImage");

    return res.status(201).json(populatedPost);
  } catch (error) {
    console.error("uploadPost error", error);
    return res.status(500).json({ message: `uploadPost error ${error.message}` });
  }
};

export const getAllPosts = async (req, res) => {
  try {
    const posts = await Post.find({})
      .populate("author", "name location profileImage")
      .populate({
        path: "comments.author",
        select: "name profileImage",
      })
      .select("caption media location author likes comments") 
      .sort({ createdAt: -1 });

    return res.status(200).json(posts);
  } catch (error) {
    return res.status(500).json({ message: `getallpost error ${error}` });
  }
};


export const toggleLikePost = async (req, res) => {
  try {
    const { postId } = req.params;
    const userId = req.userId;

    const post = await Post.findById(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    const isLiked = post.likes.some(id => id.toString() === userId.toString());

    if (isLiked) {
      post.likes = post.likes.filter(id => id.toString() !== userId.toString());
    } else {
      post.likes.push(userId);
    }

    await post.save();

    const populatedPost = await Post.findById(postId)
      .populate("author", "name profileImage")
      .populate({
        path: "comments.author",
        select: "name profileImage",
      });

    return res.status(200).json(populatedPost);
  } catch (error) {
    return res.status(500).json({ message: `toggleLikePost error ${error}` });
  }
};


export const addComment = async (req, res) => {
  try {
    const { postId } = req.params;
    const { message } = req.body;
    const userId = req.userId;

    const post = await Post.findById(postId);
    if (!post) return res.status(404).json({ message: "Post not found" });

    post.comments.push({ author: userId, message });
    await post.save();

    const populatedPost = await Post.findById(postId)
  .populate("author", "name profileImage")
  .populate({
    path: "comments.author",
    select: "name profileImage",
  })
  .select("caption media location author likes comments"); 

    return res.status(201).json(populatedPost);
  } catch (error) {
    return res.status(500).json({ message: `addComment error ${error}` });
  }
};



export const deleteComment = async (req, res) => {
    try {
        const { postId, commentId } = req.params;
        const userId = req.userId;

        const post = await Post.findById(postId);
        if (!post) return res.status(404).json({ message: "Post not found" });

        const comment = post.comments.id(commentId);
        if (!comment) return res.status(404).json({ message: "Comment not found" });

        if (comment.author.toString() !== userId.toString()) {
            return res.status(403).json({ message: "Not authorized to delete this comment" });
        }

        comment.remove();
        await post.save();

        const populatedPost = await Post.findById(postId)
  .populate("author", "name profileImage")
  .populate({
    path: "comments.author",
    select: "name profileImage",
  })
  .select("caption media location author likes comments");

        return res.status(200).json(populatedPost);
    } catch (error) {
        return res.status(500).json({ message: `deleteComment error ${error}` });
    }
}









