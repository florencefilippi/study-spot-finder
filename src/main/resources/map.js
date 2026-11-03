// 1. Initialize map centered on Vancouver
const initialLat = 49.28273;
const initialLng = -123.12074;
const map = L.map('map').setView([initialLat, initialLng], 15);

const cafeGeoJSONPath = 'data/vancouver-cafes.geojson'; 
const sfuGeoJSONPath = 'data/sfu-studySpots.geojson'; 

// Initialize a Layer Group for markers
const markersLayer = L.layerGroup().addTo(map);

// 2. Add OpenStreetMap tile layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

//Global variables
let cafeGeojsonData = null;
let sfuGeojsonData = null;


async function loadAllData() {
    try {
        // Fetch both files concurrently
        const [cafeRes, sfuRes] = await Promise.all([
            fetch(cafeGeoJSONPath),
            fetch(sfuGeoJSONPath)
        ]);

        if (!cafeRes.ok || !sfuRes.ok) {
            throw new Error("Failed to load one or more GeoJSON files.");
        }

        cafeGeojsonData = await cafeRes.json();
        sfuGeojsonData = await sfuRes.json();

        // Render markers initially based on default checked boxes
        filterAndRenderMarkers();

    } catch (error) {  
        console.error("Error loading GeoJSON files:", error);
    }
