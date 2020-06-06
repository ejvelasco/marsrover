import * as React from 'react';

export const App = () => {
  const [images, setImages] = React.useState([]);
  React.useEffect(() => {
    async function fetchData() {
      const url = '/api/v1/rovers/curiosity/images/?date=2020-06-01';
      const res = await fetch(url);
      const data = await res.json();
      setImages(data.photos);
    }
    fetchData();
  });

  // setImages(data);
  return (
    <div>
      <h1>React App!</h1>
      {images.map((image) => (
        <p>{image.img_src}</p>
      ))}
    </div>
  );
};
