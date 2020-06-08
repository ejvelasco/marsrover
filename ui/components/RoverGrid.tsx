import * as React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import {
  Grid,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@material-ui/core';
import { RoverImageCard } from './RoverImageCard';

const dates = ['02/27/17', 'June 2, 2018', 'Jul-13-2016', 'April 31, 2018'];
const defaultDate = '2020-06-04';
const defaultRover = 'curiosity';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(2),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
  },
}));

export const RoverGrid = () => {
  const classes = useStyles();
  const [selectedDate, setSelectedDate] = React.useState(new Date(defaultDate));
  const [selectedRover, setSelectedRover] = React.useState(defaultRover);
  const handleDateChange = (date) => {
    setSelectedDate(date);
  };
  const formatDate = (selectedDate: Date) => {
    return selectedDate.toISOString().split('T')[0];
  };

  return (
    <div className={classes.root}>
      <Grid container spacing={4}>
        {dates.map((date) => (
          <Grid item xs={6} sm={3} key={date}>
            <RoverImageCard date={date} maxWidth={300} rover={defaultRover} />
          </Grid>
        ))}
        <Grid item xs={6} sm={3}>
          <RoverImageCard
            date={formatDate(selectedDate)}
            maxWidth={300}
            rover={selectedRover}
          />
        </Grid>
        <Grid>
          <FormControl className={classes.formControl}>
            <InputLabel id="demo-simple-select-helper-label">
              Select Rover
            </InputLabel>
            <Select
              value={selectedRover}
              labelId="demo-simple-select-helper-label"
              onChange={(event) => {
                setSelectedRover(event.target.value as string);
              }}
            >
              <MenuItem value={'curiosity'}>Curiosity</MenuItem>
              <MenuItem value={'opportunity'}>Opportunity</MenuItem>
              <MenuItem value={'spirit'}>Spirit</MenuItem>
            </Select>
          </FormControl>
          <FormControl className={classes.formControl}>
            <TextField
              label="Select Date"
              type="date"
              defaultValue={selectedDate}
              InputLabelProps={{
                shrink: true,
              }}
              onChange={(event) => {
                setSelectedDate(new Date(event.target.value));
              }}
            />
          </FormControl>
        </Grid>
      </Grid>
    </div>
  );
};
