// 1. Initialize map centered on Vancouver
const initialLat = 49.28273;
const initialLng = -123.12074;
const map = L.map('map').setView([initialLat, initialLng], 15);

const cafeGeoJSONPath = 'data/vancouver-cafes.geojson'; 
const sfuGeoJSONPath = 'data/sfu-studySpots.geojson'; 

// Initialize a Layer Group for markers
const markersLayer = L.layerGroup().addTo(map);
const contributorIcon = L.divIcon({
    className: 'contributor-marker',
    html: '<svg width="25" height="41" viewBox="0 0 25 41" xmlns="http://www.w3.org/2000/svg">' +
          '<path d="M12.5 0C5.6 0 0 5.6 0 12.5 0 21.9 12.5 41 12.5 41S25 21.9 25 12.5C25 5.6 19.4 0 12.5 0z" ' +
          'fill="#2e7d32" stroke="#1b5e20" stroke-width="1.5"/>' +
          '<circle cx="12.5" cy="12.5" r="5" fill="white"/></svg>',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34]
});


const currentUserEl = document.getElementById('currentUser');
const currentUser = currentUserEl ? currentUserEl.textContent.trim() : '';

// 2. Add OpenStreetMap tile layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

//Initialize Search Control attached to markersLayer
const searchControl = new L.Control.Search({
    layer: markersLayer,
    propertyName: 'name',          
    initial: false,                // Matches substring anywhere in the name, not just start
    zoom: 17,
    collapsed: false,               // Keep the search bar expanded
    marker: false,                 
    textPlaceholder: 'Search cafes & study spots...',
    moveToLocation: function (latlng, title, map) {
        map.setView(latlng, 17);
    }
});

// Open the popup automatically when a search result is clicked
searchControl.on('search:locationfound', function (e) {
    if (e.layer._popup) {
        e.layer.openPopup();
    }
});

map.addControl(searchControl);

//Global variables
let cafeGeojsonData = null;
let sfuGeojsonData = null;
let spotGeojsonData = null; 


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
}

// It is separate from loadAllData so a problem here cannot break the cafes and SFU spots.
async function loadSpots() {
    try {
        const res = await fetch('/api/spots');
        if (!res.ok) {
            throw new Error("Failed to load contributor spots.");
        }
        spotGeojsonData = await res.json();
        filterAndRenderMarkers();
    } catch (error) {
        console.error("Error loading contributor spots:", error);
    }
}

function addGeoJSONToLayer(geojsonData, filterFunction = null, icon = null) {
    L.geoJSON(geojsonData, {
                pointToLayer: function (feature, latlng) {
            const chosen = typeof icon === 'function' ? icon(feature) : icon;
            return L.marker(latlng, chosen ? { icon: chosen } : {});
        },
        filter: filterFunction,
        onEachFeature: onEachFeature
    }).addTo(markersLayer);
}

// spot names and descriptions, so without this someone could put HTML or a script
// in a name and it would run for everyone who opens the popup.
function escapeHtml(text) {
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function onEachFeature(feature, layer) {
    const props = feature.properties || {};
    const name = props.name || 'Unnamed Location';

    let lat, lon;
    if (feature.geometry.type === 'Point') {
        lon = feature.geometry.coordinates[0];
        lat = feature.geometry.coordinates[1];
    } else {
        const center = layer.getBounds().getCenter();
        lat = center.lat;
        lon = center.lng;
    }

    const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(name)}+(${lat},${lon})`;

    // url that goes to a location's details page (only for features with an id)
    const detailsUrl = feature.id ? `/locations/details?id=${encodeURIComponent(feature.id)}` : '';

    // description and owner only exist on contributor spots, so they only show for those.
    layer.bindPopup(`
        <div style="font-family: sans-serif;">
            <h3 style="margin: 0 0 5px 0; font-size: 16px;">${escapeHtml(name)}</h3>
            ${props.type ? `<p style="margin: 0 0 3px 0; font-size: 12px;"><b>Type:</b> ${escapeHtml(props.type)}</p>` : ''}
            ${props.opening_hours ? `<p style="margin: 0 0 5px 0; font-size: 12px;"><b>Hours:</b> ${escapeHtml(props.opening_hours)}</p>` : ''}
            ${props.description ? `<p style="margin: 0 0 5px 0; font-size: 12px;">${escapeHtml(props.description)}</p>` : ''}
            ${props.owner ? `<p style="margin: 0 0 5px 0; font-size: 12px;"><b>Added by:</b> ${escapeHtml(props.owner)}</p>` : ''}
            <a href="${googleMapsUrl}" target="_blank" rel="noopener noreferrer" style="color: #0078A8;">
                Open in Google Maps ↗
            </a>
            ${detailsUrl ? 
                `<a href="${detailsUrl}" style="color: #0078A8; display: block;">View Details &amp; Reviews</a>` : ''
            }
        </div>
    `);
}

loadAllData();
loadSpots(); 


//filter function and load edits
function filterAndRenderMarkers() {
    // Clear current markers from the map layer group
    markersLayer.clearLayers();

    // 1. Check if the Cafe toggle is checked
    const cafesToggle = document.getElementById('cafeToggle');
    if (cafesToggle && cafesToggle.checked && cafeGeojsonData) {
        addGeoJSONToLayer(cafeGeojsonData);
    }

    const sfuToggle = document.getElementById('SFUToggle');
    if (sfuToggle && sfuToggle.checked && sfuGeojsonData) {
        // Gather checked sub-categories
        const sfuCheckboxes = document.querySelectorAll('#filterForm input[name="category"]:checked');
        const checkedTypes = Array.from(sfuCheckboxes).map(cb => cb.value.toLowerCase());

        // Only render if at least one sub-category is checked
        if (checkedTypes.length > 0) {
            addGeoJSONToLayer(sfuGeojsonData, function(feature) {
                const props = feature.properties || {};
                const featureType = (props.type || '').toLowerCase();
                
                // Return true if the feature matches ANY of the checked SFU categories
                return checkedTypes.some(type => featureType.includes(type));
            });
        }
    }

    // show the contributor spots when their toggle is checked
    const contribToggle = document.getElementById('contributorToggle');
    if (contribToggle && contribToggle.checked && spotGeojsonData) {
         addGeoJSONToLayer(spotGeojsonData, null, function (feature) {
            return feature.properties.owner === currentUser ? contributorIcon : null;
        });
    }
    
}

//event checks to see if the map needs update
const cafeToggle = document.getElementById('cafeToggle');
if (cafeToggle) {
    cafeToggle.addEventListener('change', filterAndRenderMarkers);
}

const sfuToggle = document.getElementById('SFUToggle');
if (sfuToggle) {
    sfuToggle.addEventListener('change', filterAndRenderMarkers);
}

// redraw when the contributor spots toggle changes
const contributorToggle = document.getElementById('contributorToggle');
if (contributorToggle) {
    contributorToggle.addEventListener('change', filterAndRenderMarkers);
}

// Listen for changes on all SFU category checkboxes
document.querySelectorAll('#filterForm input[name="category"]').forEach(cb => {
    cb.addEventListener('change', filterAndRenderMarkers);
});