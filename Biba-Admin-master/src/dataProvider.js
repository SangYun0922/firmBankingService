import { fetchUtils } from "react-admin";
import { stringify } from "query-string";

const apiUrl = "http://localhost:3000";
const httpClient = fetchUtils.fetchJson;

export default {
  getList: (resource, params) => {
    const { page, perPage } = params.pagination;
    const { field, order } = params.sort;
    const { q } = params.filter;
    console.log(":::::page::::", page);
    console.log(":::::q:::::", q);
    console.log("::::perPage:::::", perPage);

    // Pagination and sort
    const query = `limit=${perPage}&page=${page}&orderBy=${field}&orderDir=${order}&search=${q}`;
    console.log("::::query:::::", query);

    const url = `${apiUrl}/${resource}?${query}`;
    console.log(":::::url::::", url);
    return httpClient(url).then(({ headers, json }) => ({
      data: json,
      total: parseInt(headers.get("X-Total-Count").split("/").pop(), 10),
    }));
  },

  getOne: (resource, params) =>
    httpClient(`${apiUrl}/read/${resource}/${params.id}`).then(({ json }) => ({
      data: json,
    })),

  // getMany: (resource, params) => {
  //   console.log("::::::resource:::::::", resource);
  //   console.log("::::::params:::::::", params);

  //   const query = {
  //     filter: JSON.stringify({ id: params.ids }),
  //   };
  //   const url = `${apiUrl}/${resource}?${stringify(query)}`;
  //   return httpClient(url).then(({ json }) => ({ data: json }));
  // },

  update: (resource, params) =>
    httpClient(`${apiUrl}/${resource}/${params.id}`, {
      method: "PUT",
      body: JSON.stringify(params.data),
    }).then(({ json }) => ({ data: json })),

  updateMany: (resource, params) => {
    const query = {
      filter: JSON.stringify({ id: params.ids }),
    };
    return httpClient(`${apiUrl}/${resource}?${stringify(query)}`, {
      method: "PUT",
      body: JSON.stringify(params.data),
    }).then(({ json }) => ({ data: json }));
  },

  create: (resource, params) =>
    httpClient(`${apiUrl}/${resource}`, {
      method: "POST",
      body: JSON.stringify(params.data),
    }).then(({ json }) => ({
      data: { ...params.data, id: json.id },
    })),

  delete: (resource, params) =>
    httpClient(`${apiUrl}/${resource}/${params.id}`, {
      method: "DELETE",
    }).then(({ json }) => ({ data: json })),

  deleteMany: (resource, params) => {
    const query = {
      filter: JSON.stringify({ id: params.ids }),
    };
    console.log("deleteMany", query);
    return httpClient(`${apiUrl}/${resource}/${stringify(query)}`, {
      method: "DELETE",
      body: JSON.stringify(params.ids),
    }).then(({ json }) => ({ data: [...params.ids] }));
  },
};
