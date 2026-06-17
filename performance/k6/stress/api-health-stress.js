import http from 'k6/http';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '30s', target: 50 },
    { duration: '30s', target: 100 },
  ],
};

export default function () {
  http.get('http://httpbin.org/status/200');
}
