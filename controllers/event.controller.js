// getEventsController.js

import dotenv from 'dotenv';
dotenv.config();

import { GoogleGenerativeAI } from '@google/generative-ai';


const fetchEvents = (category, location) => {
    const mockEvents = {
        'tech': {
            'london': [
                { id: 1, name: 'London Tech Summit', date: '2025-10-20', location: 'London', category: 'Tech' },
                { id: 2, name: 'AI Innovations Meetup', date: '2025-11-05', location: 'London', category: 'Tech' },
            ],
            'paris': [
                { id: 3, name: 'Paris Web3 Conference', date: '2025-09-30', location: 'Paris', category: 'Tech' }
            ]
        },
        'music': {
            'london': [
                { id: 4, name: 'London Jazz Festival', date: '2025-10-15', location: 'London', category: 'Music' }
            ],
            'new york': [
                { id: 5, name: 'NY Rockfest', date: '2025-11-10', location: 'New York', category: 'Music' },
            ]
        },
        // ADDED A NEW CATEGORY FOR SPORTS
        'sports': {
            'new york': [
                { id: 6, name: 'New York Marathon', date: '2025-11-02', location: 'New York', category: 'Sports' }
            ]
        }
    };

    const events = (mockEvents[category] && mockEvents[category][location]) || [];
    return events;
};


const tool = {
    functionDeclarations: [
        {
            name: 'fetch_events',
            description: 'Retrieves a list of events based on category and location.',
            parameters: {
                type: 'object',
                properties: {
                    category: {
                        type: 'string',
                        description: 'The category of the event (e.g., "tech", "music", "sports").',
                    },
                    location: {
                        type: 'string',
                        description: 'The location of the event (e.g., "London", "New York").',
                    },
                },
                required: ['category', 'location'],
            },
        },
    ],
};


const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);
const model = genAI.getGenerativeModel({
    model: 'gemini-2.5-flash',
    tools: [tool],
});


const getEventsController = async (req, res) => {
    const { category, location } = req.body;

    if (!category || !location) {
        return res.status(400).json({ error: 'Both category and location are required.' });
    }

    try {
        const result = await model.generateContent({
            contents: [{
                role: 'user',
                parts: [{
                    text: `Find events in the category "${category}" in "${location}".`
                }]
            }],
        });
        
        const functionCall = result.response.candidates?.[0]?.content?.parts?.[0]?.functionCall;
        
        if (functionCall && functionCall.name === 'fetch_events') {
            const { category: extractedCategory, location: extractedLocation } = functionCall.args;
            const events = fetchEvents(extractedCategory.toLowerCase(), extractedLocation.toLowerCase());
            
            return res.status(200).json({
                message: 'Events fetched successfully.',
                data: events,
            });
        } else {
            return res.status(404).json({
                message: 'Could not find relevant events based on the request.',
                data: [],
            });
        }
    } catch (error) {
        console.error('Error in get events controller:', error);
        return res.status(500).json({ error: 'Internal server error.' });
    }
};


export default getEventsController;