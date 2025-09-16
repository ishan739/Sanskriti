
import { GoogleGenerativeAI } from '@google/generative-ai';
import dotenv from 'dotenv';
import { findEvents } from '../config/eventApi.js';

dotenv.config();

const genAI = new GoogleGenerativeAI(process.env.GENAPI_KEY);

export const eventFinder = async (req, res) => {
  try {
    const { category, location } = req.body;
    
    const userQuery = `Find all upcoming ${category} events in ${location}.`;

    const model = genAI.getGenerativeModel({
      model: 'gemini-1.5-flash-latest',
      tools: {
        function_declarations: [
          {
            name: 'findEvents',
            description: 'Finds upcoming events based on category and location.',
            parameters: {
              type: 'object',
              properties: {
                category: {
                  type: 'string',
                  description: 'The category of the event, like "Music" or "Tech".',
                },
                location: {
                  type: 'string',
                  description: 'The location of the event, like "Ghaziabad" or "Noida".',
                },
              },
              required: ['category', 'location'],
            },
          },
        ],
      },
    });

    let result;
    const maxRetries = 3;
    const retryDelay = 2000; // 2 seconds

    for (let i = 0; i < maxRetries; i++) {
      try {
        result = await model.generateContent({
          contents: [{ role: 'user', parts: [{ text: userQuery }] }],
        });
        break; 
      } catch (error) {
        if (error.status === 503 && i < maxRetries - 1) {
          console.log(`Model is overloaded. Retrying in ${retryDelay / 1000} seconds...`);
          await new Promise(resolve => setTimeout(resolve, retryDelay));
        } else {
          throw error; 
        }
      }
    }

    if (!result || !result.response) {
      return res.status(500).json({ error: 'Failed to get a response from the Gemini API.' });
    }

    const call = result.response.functionCalls();
    
    if (call && call.name === 'findEvents') {
      const toolResponse = await findEvents(call.args);
      return res.json(toolResponse);
    } else {
      return res.status(400).json({ error: 'Sorry, I could not find events for that query. Please specify a category and location.' });
    }

  } catch (error) {
    console.error('Error fetching events:', error);
    res.status(500).json({ error: 'Failed to retrieve events.' });
  }
};