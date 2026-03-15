window.onload = function () {
  window.ui = SwaggerUIBundle({
    url: "/metrics-servers/api/openapi",
    dom_id: "#swagger-ui",
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    layout: "StandaloneLayout"
  });
};
