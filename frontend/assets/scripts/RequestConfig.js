export default class RequestConfig {
    
    constructor() {
        this.method = 'GET';
        this.headers = {
            'Accept': 'application/json',
        };
    }

    post() {
        this.method = 'POST';
        return this;
    }

    get() {
        this.method = 'GET';
        return this;
    }

    put() {
        this.method = 'PUT';
        return this;
    }

    delete() {
        this.method = 'DELETE';
        return this;
    }

    defaultAuth() {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            this.headers['Authorization'] = `Bearer ${token}`;
        }
        return this;
    }

    withAuth() {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            this.headers['Authorization'] = `Bearer ${token}`;
        }
        return this;
    }

    withHeader(key, value) {
        this.headers[key] = value;
        return this;
    }
    
    jsonBody(body) {
        this.body = JSON.stringify(body);
        return this.asJson();
    }

    asJson() {
        this.headers['Content-Type'] = 'application/json';
        return this;
    }
}