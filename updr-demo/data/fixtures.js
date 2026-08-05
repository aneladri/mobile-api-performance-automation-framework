'use strict';

function initialState() {
  return {
    clients: [{ clientId: 'CLIENT-001', name: 'NorthStar Property Services' }],
    templates: [],
    assignments: [],
    orders: [{
      orderId: 'UPDR-1001',
      clientId: 'CLIENT-001',
      inspectorId: 'INSPECTOR-001',
      status: 'ASSIGNED',
      property: {
        address: '125 Market Street', city: 'Charlotte', state: 'NC',
        postalCode: '28202', propertyType: 'Single Family', yearBuilt: 1998,
        bedrooms: 3, bathrooms: 2
      },
      mls: { listingId: 'MLS-88421', squareFeet: 2150, listPrice: 465000 }
    }],
    inspections: [{
      inspectionId: 'INSP-1001', orderId: 'UPDR-1001', templateId: null,
      status: 'DRAFT', syncStatus: 'NOT_STARTED', autosaveAttempts: 0,
      submissionAttempts: 0, qcCycle: 0, qcComment: null,
      validationErrors: [],
      sections: {
        summary: {}, exterior: {}, interior: {}, photos: [], voiceNotes: [], floorScan: null
      },
      aiEvidence: { photo: null, voice: null, humanConfirmation: false },
      audit: [{ action: 'INSPECTION_CREATED', actor: 'SYSTEM', timestamp: new Date().toISOString() }]
    }]
  };
}

const initialPhoto = {
  subject: 'EXTERIOR_WALL', material: 'BRICK', defect: 'STRUCTURAL_CRACK',
  severity: 'MEDIUM', qualityScore: 72, confidence: 0.74,
  requiresConfirmation: true, duplicate: false, provider: 'MAPAF_REPLAY_CV'
};
const duplicatePhoto = { ...initialPhoto, qualityScore: 72, duplicate: true, duplicateOf: 'PHOTO-INITIAL' };
const replacementPhoto = {
  subject: 'EXTERIOR_WALL', material: 'BRICK', defect: 'STRUCTURAL_CRACK',
  severity: 'MEDIUM', qualityScore: 96, confidence: 0.95,
  requiresConfirmation: false, duplicate: false, provider: 'MAPAF_REPLAY_CV'
};
const voiceAnalysis = {
  transcript: 'Water damage is visible beneath the kitchen window. The affected area is approximately two feet wide.',
  mappedFields: {
    location: 'KITCHEN_WINDOW', defect: 'WATER_DAMAGE', severity: 'HIGH', affectedArea: '2 feet'
  },
  confidence: 0.91, provider: 'MAPAF_REPLAY_TRANSCRIPTION'
};

module.exports = { initialState, initialPhoto, duplicatePhoto, replacementPhoto, voiceAnalysis };
