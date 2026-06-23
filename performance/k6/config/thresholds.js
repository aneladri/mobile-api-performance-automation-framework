export const THRESHOLDS = {

    smoke: {
        http_req_duration: ['p(95)<3000'],
        http_req_failed: ['rate<0.05'],
    },

    load: {
        http_req_duration: ['p(95)<2000'],
        http_req_failed: ['rate<0.01'],
    },

    stress: {
        http_req_duration: ['p(95)<5000'],
        http_req_failed: ['rate<0.05'],
    }
};
