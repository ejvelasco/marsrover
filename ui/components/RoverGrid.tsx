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

const formatDate = (selectedDate: Date) => {
  return selectedDate.toISOString().split('T')[0];
};

function isValidDate(dateString) {
  var regEx = /^\d{4}-\d{2}-\d{2}$/;
  if (!dateString.match(regEx)) return false; // Invalid format
  var d = new Date(dateString);
  var dNum = d.getTime();
  if (!dNum && dNum !== 0) return false; // NaN value, Invalid date
  return d.toISOString().slice(0, 10) === dateString;
}

export const RoverGrid = () => {
  const classes = useStyles();
  const [selectedDate, setSelectedDate] = React.useState(new Date(defaultDate));
  const [selectedRover, setSelectedRover] = React.useState(defaultRover);

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
                const date = event.target.value;
                if (!isValidDate(date)) {
                  return;
                }
                setSelectedDate(new Date(date));
              }}
            />
          </FormControl>
        </Grid>
      </Grid>
    </div>
  );
};
