export default class FetchInterceptor {
    static create(atInterceptionEvent) {
        const { fetch: originalFetch } = window;

        window.fetch = async (...args) => {
            try {
                const response = await originalFetch(...args);
                atInterceptionEvent(response.clone());
                return response;
            } catch (error) {
                atInterceptionEvent(error);
                throw error;
            }
        };
    }

    static handleUnauthorized(response) {
        if (response.status === 401) {
            console.log('Token expirado ou não autorizado. Redirecionando para login...');
            window.location.href = '/login';
        }
    }
}