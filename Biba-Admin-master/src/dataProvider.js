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
    // Filter?
    console.log("::::query:::::", query);

    const url = `${apiUrl}/${resource}?${query}`;
    console.log(":::::url::::", url);
    return httpClient(url).then(({ headers, json }) => ({
      data: json,
      total: parseInt(headers.get("X-Total-Count").split("/").pop(), 10),
    }));
  },
  // getList: (resource, params) => {
  //   const { page, perPage } = params.pagination;
  //   const { field, order } = params.sort;
  //   const query = {
  //     sort: JSON.stringify([field, order]),
  //     range: JSON.stringify([(page - 1) * perPage, page * perPage - 1]),
  //     filter: JSON.stringify(params.filter),
  //   };
  //   const url = `${apiUrl}/${resource}?${stringify(query)}`;

  //   return httpClient(url).then(({ headers, json }) => ({
  //     data: json,
  //     total: parseInt(headers.get("X-Total-Count").split("/").pop(), 10),
  //   }));
  // },

  // getOne: (resource, params) =>
  //   httpClient(`${apiUrl}/${resource}/${params.id}`).then(({ json }) => ({
  //     data: json,
  //   })),

  // getMany: (resource, params) => {
  //   console.log("::::::resource:::::::", resource);
  //   console.log("::::::params:::::::", params);

  //   const query = {
  //     filter: JSON.stringify({ id: params.ids }),
  //   };
  //   const url = `${apiUrl}/${resource}?${stringify(query)}`;
  //   return httpClient(url).then(({ json }) => ({ data: json }));
  // },

  // getManyReference: (resource, params) => {
  //   console.log("::::::resource:::::::", resource);
  //   console.log("::::::params:::::::", params);
  //   const { page, perPage } = params.pagination;
  //   const { field, order } = params.sort;
  //   const query = {
  //     sort: JSON.stringify([field, order]),
  //     range: JSON.stringify([(page - 1) * perPage, page * perPage - 1]),
  //     filter: JSON.stringify({
  //       ...params.filter,
  //       [params.target]: params.id,
  //     }),
  //   };
  //   const url = `${apiUrl}/${resource}?${stringify(query)}`;
  //   console.log(":::::::url:::::::", url);

  //   return httpClient(url).then(({ headers, json }) => ({
  //     data: json,
  //     total: parseInt(headers.get("X-Total-Count").split("/").pop(), 10),
  //   }));
  // },

  // update: (resource, params) =>
  //   httpClient(`${apiUrl}/${resource}/${params.id}`, {
  //     method: "PUT",
  //     body: JSON.stringify(params.data),
  //   }).then(({ json }) => ({ data: json })),

  // updateMany: (resource, params) => {
  //   const query = {
  //     filter: JSON.stringify({ id: params.ids }),
  //   };
  //   return httpClient(`${apiUrl}/${resource}?${stringify(query)}`, {
  //     method: "PUT",
  //     body: JSON.stringify(params.data),
  //   }).then(({ json }) => ({ data: json }));
  // },

  // create: (resource, params) =>
  //   httpClient(`${apiUrl}/${resource}`, {
  //     method: "POST",
  //     body: JSON.stringify(params.data),
  //   }).then(({ json }) => ({
  //     data: { ...params.data, id: json.id },
  //   })),

  // delete: (resource, params) => {
  //   // console.log(":::::::delete resource:::::", resource);
  //   // console.log("::::::::delete::::::", params.id);
  //   // httpClient(`${apiUrl}/${resource}/${params.id}`, {
  //   //   method: "DELETE",
  //   // }).then(({ json }) => ({ data: json }));
  //   axios
  //     .delete(`${apiUrl}/${resource}/${params.id}`)
  //     .then((data) => console.log("확인", data));
  // },
  // delete: (resource, params) =>
  //   httpClient(`${apiUrl}/${resource}/${params.id}`, {
  //     method: "DELETE",
  //   }).then(({ json }) => ({ data: json })),

  // deleteMany: (resource, params) => {
  //   const query = {
  //     filter: JSON.stringify({ id: params.ids }),
  //   };
  //   console.log("deleteMany", query);
  //   return httpClient(`${apiUrl}/${resource}/${stringify(query)}`, {
  //     method: "DELETE",
  //     body: JSON.stringify(params.ids),
  //   }).then(({ json }) => ({ date: json }));
  // },
};
