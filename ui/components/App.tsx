import * as React from 'react';
import { Typography } from '@material-ui/core';
import { RoverGrid } from './RoverGrid';

export const App = () => {
  return (
    <div>
      <Typography variant="h5" style={{ fontWeight: 'bold', margin: '10px' }}>
        MarsRover Project
      </Typography>
      <RoverGrid />
    </div>
  );
};
