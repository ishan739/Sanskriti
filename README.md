# 🌸 Sanskriti – Backend  

![Node.js](https://img.shields.io/badge/Node.js-16.x-green?logo=node.js)  
![Express](https://img.shields.io/badge/Express.js-Backend-lightgrey?logo=express)  
![MongoDB](https://img.shields.io/badge/Database-MongoDB-brightgreen?logo=mongodb)  
![License](https://img.shields.io/badge/License-MIT-blue)  
![Contributions Welcome](https://img.shields.io/badge/Contributions-Welcome-orange)  

> **Sanskriti** is a cultural mobile/web app designed to make **India’s rich heritage accessible, engaging, and interactive** for users across the world.  
> This repository contains the **Node.js + Express backend** that powers APIs for cultural exploration, marketplace, events, AR scans, and AI trip planning.  

---

## 📖 Project Overview  

Sanskriti brings together **monuments, festivals, foods, art, music, dance, and folk stories** in one platform.  

- **Frontend (Android app)** → separate repository  
- **Machine Learning APIs (FastAPI)** → separate repository  
- **Backend (this repo)** → REST APIs built using **Node.js, Express, MongoDB**  

---

## ✨ Features  

- 🕌 **Heritage Explorer** – Discover Indian monuments (filter by district)  
- 🎉 **Festivals & Foods of India** – Explore cultural cuisines & celebrations  
- 🎨 **Arts, Music & Dance** – Access India’s creative heritage  
- 📖 **Sacred Stories** – Folk tales, epics, and myths  
- 🛍️ **Sanskriti Bazar** – Buy traditional handicrafts, paintings, sculptures, jewelry  
- 📅 **Festival Calendar** – API-powered festival & holiday calendar  
- 📌 **Upcoming Events** – Stay updated on cultural happenings  
- 🤝 **Community Hub** – Upload images, like, share, and comment  
- 📷 **AR Scan (Monuments)** – Identify monuments via ML image recognition  
- 💬 **Chatbot** – Conversational guide for culture & travel  
- 🤖 **AI Trip Planner** – Personalized cultural journeys  

---

## 📂 Repository Structure  

```yaml
Sanskriti:
  config: # DB, Cloudinary, Token, Mail, Calendarific configs
    - calendarific.js
    - cloudinary.js
    - db.js
    - generateToken.js
    - mail.js

  controllers: # Business logic for modules
    - Art.controller.js
    - calender.js
    - cart.controller.js
    - Dance.controller.js
    - event.controller.js
    - Festival.controller.js
    - Food.controller.js
    - item.controller.js
    - Monument.controller.js
    - Music.controller.js
    - Post.controller.js
    - predict.js
    - Story.controller.js
    - user.controller.js

  dataset: # CSV datasets (arts, festivals, food, monuments, etc.)
    - art.csv
    - dance.csv
    - festivals.csv
    - food.csv
    - foods.csv
    - india_200_monuments.csv
    - music.csv
    - story.csv

  middlewares: # Auth, validation, multer
    - authMiddleware.js
    - isAdmin.js
    - multer.js
    - validateRequest.js

  models: # Mongoose schemas
    - art.js
    - cart.js
    - dance.js
    - Festival.js
    - food.js
    - item.js
    - Monument.js
    - music.js
    - post.js
    - story.js
    - user.js

  routes: # REST API endpoints
    - art.route.js
    - calender.route.js
    - cart.route.js
    - dance.route.js
    - event.route.js
    - festival.route.js
    - food.route.js
    - item.route.js
    - monument.route.js
    - music.route.js
    - post.route.js
    - predict.route.js
    - story.route.js
    - user.route.js

  validation: # Joi/validator schemas
    - userValidation.js

  public: # Static files
    - .gitkeep

  index.js: "App entry point"
  package.json: "Dependencies & scripts"
  .gitignore: "Ignored files"
