import axios from 'axios';

export default class DocumentService {
    API_URL = process.env.VUE_APP_DOCUMENT_SERVICE_URI;
    token = "";
    
    constructor(url, token) {
        this.API_URL = url;
        this.token = token;
    }

    
    
    getIdForDocument() {
        const url = `${this.API_URL}/FR_FR/generate/id`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            ) 
            .then((response) => {
                return response.data;
            });
    }



    getDocumentStorageById(id) {
        const url = `${this.API_URL}/FR_FR/${id}`;
        return axios
            .get(url, {
                headers: {
                    "content-type": "application/json",
                    authorization: "Bearer " + this.token,
                }}
            );
    }



    mergeDocumentStorage(id, file, onUploadProgress) {
        const url = `${this.API_URL}/FR_FR/storage/${id}`;
        let formData = new FormData();
        formData.append('file', file);
        return axios
            .post(url, formData,
                {
                headers: {
                  "content-type": "multipart/form-data",
                  authorization: "Bearer " + this.token,
                },
                onUploadProgress,
              }
            );
    }
}