export const MOBILE_HEADERS = {
    'X-Device-Type': __ENV.DEVICE_TYPE || 'mobile',
    'X-Platform': __ENV.PLATFORM || 'android',
    'X-App-Version': __ENV.APP_VERSION || 'local',
    'Content-Type': 'application/json',
    'Accept': 'application/json'
};