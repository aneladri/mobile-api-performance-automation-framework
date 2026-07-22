import http from 'k6/http';

const BASE_URL =
    __ENV.BASE_URL || 'https://httpbin.org';

export function getAuthToken() {

    const username =
        __ENV.USERNAME || 'testuser';

    const password =
        __ENV.PASSWORD || 'password';

    const response =
        http.post(
            `${BASE_URL}/login`,
            JSON.stringify({
                username,
                password
            }),
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );

    if (response.status !== 200) {
        throw new Error(
            `Authentication failed: ${response.status}`
        );
    }

    return response.json('token');
}
